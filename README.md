# GPA Calculator (JavaFX)

A desktop GPA calculator built with JavaFX. The application lets students enter course details, calculate GPA, export results, and persist their records to a local SQLite database for later review and updates.

## Features
- Collect student information with validation before entering course data
- Add, edit, or remove courses while keeping running totals of earned credits
- Calculate GPA and weighted points, and display results in a stylized certificate view
- Export results to CSV for sharing or archival
- Persist calculated results to `database.db` (SQLite)
- Browse, view, update, or delete previously saved results, including loading them back into the editor workflow

<img width=800 src="https://github.com/utsaroy/roy_2207027_GPACalculator/blob/main/out/ss_welcome.png" target="_blank" />
<img width=800 src="https://github.com/utsaroy/roy_2207027_GPACalculator/blob/main/out/ss_home.png" target="_blank" />
<img width=800 src="https://github.com/utsaroy/roy_2207027_GPACalculator/blob/main/out/ss_result.png" target="_blank" />
<img width=800 src="https://github.com/utsaroy/roy_2207027_GPACalculator/blob/main/out/ss_saved.png" target="_blank" />

## Project Structure
```
roy_2207027_GPACalculator/
├── pom.xml
├── src/
│   ├── main/java/com/utsa/advprog/roy_2207027_gpacalculator/
│   │   ├── controller classes (MainController, ResultController, ...)
│   │   ├── persistence helpers (DatabaseManager, ReadData, WriteData)
│   │   └── domain models (Student, Course, SavedResult)
│   └── main/resources/com/utsa/advprog/roy_2207027_gpacalculator/
│       ├── FXML views
│       └── stylesheet
└── database.db (created at runtime if missing)
```

## Requirements
- JDK 21 (or newer)
- IntelliJ IDEA


## Contributing
Feel free to open issues or submit pull requests with improvements.
