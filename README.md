# Streaming HTTP request

Recently, I had an interesting challenge. I would have to stream incoming request,
which were file uploads, to another service.

I found out how to do it with Play 2.5 and this repository is an example of how to
do it.

## How to ?

1. Clone the repo
2. `sbt run`
3. `open http://localhost:9000`
4. pick a file for upload and look @ the code in the `app/HomeController.scala` file
