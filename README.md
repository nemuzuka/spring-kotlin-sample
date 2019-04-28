# Spring + Kotlin Sample
Spring と Kotlin を組み合わせたサンプル

## Repository のテスト

FlywayTest を使用してます。

* [JdbcTaskRepositryTest](src/test/kotlin/net/jp/vss/sample/infrastructure/tasks/JdbcTaskRepositryTest.kt)
    * 実際に RDBMS(H2) へアクセスしてテストします

## ktlint

### フォーマット + ktlint

```sh
$ ./gradlew formatAndKtlint
```

### ktlint のみ実行

```sh
$ ./gradlew ktlint
```
