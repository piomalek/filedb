# filedb
Concurrent safe file based database exposing basic CRUD operations via REST endpoints

Configuration parameters are stored in resources/application.properties 
```
db.path=C:\\tmp\\db   # path to the folder where db files will be stored
db.colSeparator=;      # separator between id and values
db.valSeparator=|      # separator between each value in values section
```

Example db table file:
```
1;pr|r|o
2;d|uc|ts
```
## APIs exposed: 
### insert
**POST**
_http://localhost:8080/api/{tableName}_
body:
```
{
	"values" : ["pr", "r", "o"]
}
```

### select:
**GET**
_http://localhost:8080/api/{tableName}/{id}_

### update:
PUT
_http://localhost:8080/api/{tableName}/{id}_
```
body:
{
	"id": 1,
	"values" : ["g", "g", "j", "k"]
}
```
