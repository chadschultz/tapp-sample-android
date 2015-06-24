# SimplyTapp Android Code Sample

In this project you will find a gradle.build file, an (nearly empty) Android Activity, and some resource files.

Please use Gradle to build, run, or test the project. Feel free to add any dependencies that you care to.

The objective is for you to:

- Modify the source so that the application displays a screen that can accept 2 lat-lon pairs from the user and submit them
- Modify the activity(s) so that the user input can be received and passed to invoke a the weather service implementation
  (that you'd write) which RESTfully calls to the Open Weather Map (OWM) API (more on that in a minute)
- Evaluates the sunrise and sunset fields of the OWM response and returns a response to the effect:
---

 "$city1 has $n hours, $m minutes, $o seconds more sun than $city2"

 or

 "$city1 and $city2 have equal amounts of sunshine"

 or

 Some message appropriate for an edge-case that you identify

---

## Time and Detail

We know that you are busy and we are grateful that you've agreed to take the time to put some effort towards this.

If you are short on time, perhaps consider adding **// TODO** comments with your intentions and thoughts.

It'd be great if you could complete the full-stack sample, but it is important to emphasize that **our focus is on
concise, orderly, well-organized code and the potential that it demonstrates.**

---


## Open Weather Map (OWM) API Docs and sample URLs

http://openweathermap.org/weather-data


http://api.openweathermap.org/data/2.5/weather?lat=30.303315&lon=-97.729712


http://api.openweathermap.org/data/2.5/weather?lat=32.771026&lon=-96.791421



## Useful Gradle reference:

Build

./gradlew clean assembleDebug


List tasks

./gradlew tasks


Run Android App

./gradlew clean installDebug