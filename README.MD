# Charades-Server

Server for **Charades** Android app. 

## Running server

Server uses Redis as storage and cache, so to run it you need to provide address to Redis server in `redisson.yaml` config file.

There are two profiles defined: `dev` and `prod`, both with their own configuration. 
`Dev` disables SSL so no certificate required to run server locally.

You can specify active profile e.g. in run command with param: `--spring.profiles.active=[dev|prod]`
