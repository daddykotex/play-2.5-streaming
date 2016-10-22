package controllers

import akka.util.ByteString

import javax.inject._
import java.io.File

import play.api._
import play.api.mvc._
import play.api.libs.streams.Accumulator
import play.api.libs.ws.{StreamedBody, WSClient}

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ws: WSClient)(implicit val ec: ExecutionContext) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async { 
    Future.successful(Ok(views.html.index("Your new application is ready.")))
  }

  private val uploadTo = (req: RequestHeader) =>  s"http://${req.host}/receive"
  private val streamParser: BodyParser[String] = BodyParser("streaming") { request => 
	Accumulator.source[ByteString].mapFuture { source =>
		ws.url(uploadTo(request))
	       .withMethod("POST")
	       .withBody(StreamedBody(source))
	       .execute()
	       .map { resp => 
       		 resp.status match {
	       		case status if status >= 200 && status < 300 =>
	       			Right(resp.body)
	       		case _ =>
	       			Left(InternalServerError)
	       	 }
	       }
    }	
  }

  def upload = Action.async(streamParser) { implicit request =>
    Future.successful(Ok(request.body))
  }

  def receive = Action.async(parse.temporaryFile) { request =>
	  val file = request.body.moveTo(new File(s"/tmp/${request.body.file.getName}"))
	  Logger.info(s"File uploaded @${file.getAbsolutePath}")
	  Future.successful(Ok(file.getAbsolutePath))
  }

}
