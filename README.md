# Questland API
This is a public API for Questland inspired by Mandalorian007. 
Current public url: https://questland-public-api.cfapps.io/swagger-ui.html

# Purpose
Help the community build and grow. I offer informative videos on my
YouTube to help players catch up with ease to enjoy the game and
published a "handbook" that offers guides on everything you need
to know! I have also created a questland discord bot that is backed by
the questland public api. To earn exclusive perks become a Patreon by pledging
$3 or more, though it's not required, but greatly appreciated.

# Youtube
My youtube provides the basics such as Gear, a variety of builds,
and the Battle Events.
https://www.youtube.com/channel/UCLHjCEBoRj-PGCPvmWzQXMQ/videos

# Questland Handbook
This website provides extensive guides and in depth information on everything you need to know about Questland.
https://questland-handbook.cfapps.io/

# Discord Bot
Repository available at: https://github.com/Mandalorian007/questland-discord-bot
Discord installation link: https://discordapp.com/oauth2/authorize?client_id=675765765395316740&scope=bot

# Patreon
Patreon allows me to do what I love while supporting my projects
and creations by pledging monthly. With your generosity and support 
through Patreon, I can take these projects even further.
https://www.patreon.com/thundersoap

#Developers

## Local Setup
- Java 11 JDK is needed
- Protocol Buffers is used so you need flatc installed on your PC
- run: `gradlew createFlatBuffers`
- Questland API tokens for each server need as well as the google oauth client id to support user login to be setup, and your spring profile configured to `dev` in your IDE or on your system to run locally: 
```yaml
GLOBAL_PLAYER_TOKEN
EUROPE_PLAYER_TOKEN
AMERICA_PLAYER_TOKEN
ASIA_PLAYER_TOKEN
VETERANS_PLAYER_TOKEN
GOOGLE_OAUTH_CLIENT_ID
spring.profiles.active
```
- when running locally ensure you setup application default credentials: `gcloud auth application-default login`
- to start the application run: `gradlew bootRun`


### Deployment
- Deployment is done via appengine so you need a gcp project and to update your GCP project in `gradle.build`
- you need a local environment variables file `src/main/appengine/env_variables.yaml` with Questland API tokens
```$yaml
env_variables:
  GLOBAL_PLAYER_TOKEN: 'TOKEN_GOES_HERE'
  EUROPE_PLAYER_TOKEN: 'TOKEN_GOES_HERE'
  AMERICA_PLAYER_TOKEN: 'TOKEN_GOES_HERE'
  ASIA_PLAYER_TOKEN: 'TOKEN_GOES_HERE'
  VETERANS_PLAYER_TOKEN: 'TOKEN_GOES_HERE'
  GOOGLE_OAUTH_CLIENT_ID: 'GOOGLE_OAUTH_CLIENT_ID_GOES_HERE'
  spring.profiles.active: 'production'
```
- run: `gradlew bootJar`
- run: `gradlew appengineDeploy`