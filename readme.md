# jpa-plus
## What is it?
this project is used for making it easy to use specification of spring data jpa, you can build specification use fluent api

##  How to install?

### build with gradle

```groovy
compile 'io.github.ThomasChant:jpa-plus-core:${latestVersion}'
```

### build with maven

```xml
<dependency>
            <groupId>io.github.ThomasChant</groupId>
            <artifactId>jpa-plus-core</artifactId>
            <version>${latestVersion}</version>
</dependency>
```



## Fluent api

| Api                             | Remark                                              |
| ------------------------------- | --------------------------------------------------- |
| eq(field, val)                  | query data with field equal to val                 |
| notEq(field, val)               | query data with field not equal to val             |
| gt(field, val)                  | query data with field greater than val             |
| ge(field, val)                  | query data with field greater than or equal to val |
| lt(field, val)                  | query data with field less than val                |
| le(field, val)                  | query data with field less than or equal to val    |
| allLike(field, val)             | query data with field contain val                  |
| leftLike(field, val)            | query data with field end with val                 |
| rightLike(field, val)           | query data with field start with val               |
| isNull(field)                   | query data with field is null                      |
| isNotNull(field,)               | query data with field is not null                  |
| in(field, collection)           | query data with field in collection                |
| notIn(field, collection)        | query data with field not in collection            |
| in(field, array)                | query data with field  in array                    |
| notIn(field, array)             | query data with field  not  in array               |
| between(field, lower, upper)    | query data with field  between lower and upper     |
| notBetween(field, lower, upper) | query data with field  not between lower and upper |

## Example

### Domain entity

```java
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(generator = "strategy",strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String realname;
}
```



### Repository

```java
@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
}
```

### Build Specification

#### Use common fluent api 

```java
Specification<User> spec = Conditions.use(User.class)
                .eq("id", 2)
                .and(i->i.eq("username", "lisi"))
                .eq("realname", "李四")
                .toSpec();
```

#### Use lambda api

```java
Specification<User> spec = Conditions.lambdaUse(User.class)
                .eq(User::getId, 2L)
                .and(i->i.eq(User::getUsername, "lisi"))
                .and()
                .eq(User::getRealname, "李四")
                .toSpec();
```

### Use specification

```java
  List<User> all = userRepository.findAll(spec);
```

