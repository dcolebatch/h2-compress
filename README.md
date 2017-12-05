# H2Compress
A compressor for H2 databases that just grow and grow and grow :P


## Synopsis
This app will read one h2 database file, and write to a new one.
Use this to compress/defragment an otherwise leaky database, usually 
from long-running h2 processes.

Usage:

```
  java h2compress src_db.h2 dst_db.h2
```

## Development
We're using Maven 3.x here:

```
brew install maven
mvn package
```

** Profit **


To watch for filesystem changes as you go, use:

```
mvn fizzed-watcher:run
```


## Distribution
Build one self-contained JAR with:

```
mvn clean compile assembly:single
```
