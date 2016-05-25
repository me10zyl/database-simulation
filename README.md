# Database Simulation
这是一个**简易数据库模拟**代码，通过特定格式来存储数据，使用查询语句来获取这些数据，用于了解数据库各个语句是如何工作的？<br>
##现在支持的SQL语句
+ `select * from table`
+ `inner join`
+ `left join`
+ `right join`
+ `where`

##数据格式
数据存储在`/db/`文件夹下，每个`table`以独立的一个文件形式存在，第一行`col1`、`col2`、`col3`为字段名称，后面的三行分别是三条记录。
```
col1 col2 col3
1     1     1
2     2     2
3     3     3
```
