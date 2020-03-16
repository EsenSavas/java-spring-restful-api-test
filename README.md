# Hunter Six - Java Spring RESTful API Test

This project uses both Gradle and Maven, for ease of use by the developer undertaking the assessment.

## How to test
### Using Gradle
```./gradlew test```

### Using Maven
```mvn test```

## Exercises
### Exercise 1
Make the ALL tests run green (there is one failing test)

### Exercise 2
Update the existing `/person/{lastName}/{firstName}` endpoint to return an appropriate RESTful response when the requested person does not exist in the list
- prove your results

### Exercise 3
Write a RESTful API endpoint to retrieve a list of all people with a particular surname
- pay particular attention to what should be returned when there are no match, one match, multiple matches
- prove your results

### Exercise 4
Write a RESTful API endpoint to add a new value to the list
- pay attention to what should be returned when the record already exists
- pay attention to what information is supplied to the calling client
- prove your resutls

### Exercise 5
Write a RESTful API endpoint to update ONLY the first name (partial update)
- pay attention to what information is supplied to the calling client
- prove your results
