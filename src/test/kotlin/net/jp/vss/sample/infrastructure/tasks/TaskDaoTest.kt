package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.JdbcRepositoryUnitTest
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import java.util.UUID

/**
 * TaskDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class TaskDaoTest {

    @Autowired
    private lateinit var sut: TaskDao

    @Test
    @FlywayTest
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val task = TaskEntityFixtures.create()

        // execution
        val actual = sut.create(task)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(task)
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト(一意制約)")
    fun testCreate_Duplicate() {
        // setup
        val task = TaskEntityFixtures.create()
        sut.create(task)

        // execution
        val actual = catchThrowable {
            sut.create(task.copy(taskId = UUID.randomUUID().toString())) // task_code の一位制約違反
        }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    @DisplayName("findByTaskCode のテスト(全てのプロパティが存在する)")
    fun testFindByTaskCode_AllProperties() {
        // setup
        val taskCode = "task_code_001"

        // execution
        val actual = sut.findByTaskCode(taskCode, SelectOptions.get())

        // verify
        val expected = TaskEntity(taskId = "task_id_001",
            taskCode = "task_code_001",
            status = "OPEN",
            title = "件名その1",
            contentValue = "内容その1",
            deadline = 1246732800001L,
            attributes = """{"hige":"hage"}""",
            createUserCode = "create_user_001",
            createAt = 1646732800001,
            lastUpdateUserCode = "last_update_user_001",
            lastUpdateAt = 1746732800001,
            versionNo = 1L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    @DisplayName("findByTaskCode のテスト(NotNullプロパティのみ)")
    fun testFindByTaskCode_NullProperties() {
        // setup
        val taskCode = "task_code_002"

        // execution
        val actual = sut.findByTaskCode(taskCode, SelectOptions.get())

        // verify
        val expected = TaskEntity(taskId = "task_id_002",
            taskCode = "task_code_002",
            status = "DONE",
            title = "件名その2",
            contentValue = "内容その2",
            deadline = 1246732800002L,
            attributes = null,
            createUserCode = "create_user_002",
            createAt = 1646732800002,
            lastUpdateUserCode = "last_update_user_002",
            lastUpdateAt = 1746732800002,
            versionNo = 2L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("該当レコードが存在しない")
    fun testFindByTaskCode_NotFound() {
        // setup
        val taskCode = "absent_task_code"

        // execution
        val actual = sut.findByTaskCode(taskCode, SelectOptions.get())

        // verify
        assertThat(actual).isNull()
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    @DisplayName("findAll のテスト")
    fun testFindAll() {
        // execution
        val actual = sut.findAll()

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].taskId).isEqualTo("task_id_001")
        assertThat(actual[1].taskId).isEqualTo("task_id_002")
    }

    @Test
    @FlywayTest
    @DisplayName("findAll のテスト(未登録)")
    fun testFindAll_Empty() {
        // execution
        val actual = sut.findAll()

        // verify
        assertThat(actual).isEmpty()
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト全てのプロパティ指定")
    fun testUpdate_AllProperties() {
        // setup
        val baseTask = TaskEntityFixtures.create()
        sut.create(baseTask)

        val task = baseTask.copy(
            taskCode = "NOT_UPDATE", // 対象外
            title = "update title",
            contentValue = "update content",
            status = "DONE",
            deadline = 1234567890L,
            attributes = """{"update_sum":1}""",
            createUserCode = "NOT_UPDATE", // 対象外
            createAt = 3234567891L, // 対象外
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)

        // execution
        val actual = sut.update(task)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        // Doma2 の update の戻り値はパラメータ +  verison をインクリメントしたもの
        // プロパティ指定の update だとちょっと使えないな...
        assertThat(actual.entity).isEqualTo(task.copy(versionNo = task.versionNo + 1))

        // 取得した時は変わっていること
        val expected = baseTask.copy(
            title = "update title",
            contentValue = "update content",
            status = "DONE",
            deadline = 1234567890L,
            attributes = """{"update_sum":1}""",
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            versionNo = baseTask.versionNo + 1) // インクリメントする
        assertThat(sut.findByTaskCode(baseTask.taskCode, SelectOptions.get())).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテストNotNullプロパティのみ指定")
    fun testUpdate_NullProperties() {
        // setup
        val baseTask = TaskEntityFixtures.create()
        sut.create(baseTask)

        val task = baseTask.copy(
            taskCode = "NOT_UPDATE", // 対象外
            title = "update title",
            contentValue = "update content",
            status = "DONE",
            deadline = null,
            attributes = null,
            createUserCode = "NOT_UPDATE", // 対象外
            createAt = 3234567891L, // 対象外
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)

        // execution
        val actual = sut.update(task)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(task.copy(versionNo = task.versionNo + 1))

        // 取得した時は変わっていること
        val expected = baseTask.copy(
            title = "update title",
            contentValue = "update content",
            status = "DONE",
            deadline = null,
            attributes = null,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            versionNo = baseTask.versionNo + 1) // インクリメントする
        assertThat(sut.findByTaskCode(baseTask.taskCode, SelectOptions.get())).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト version 不正")
    fun testUpdate_InvalidVersion() {
        // setup
        val baseTask = TaskEntityFixtures.create()
        sut.create(baseTask)
        val task = baseTask.copy(versionNo = baseTask.versionNo + 1)

        // execution
        val actual = catchThrowable { sut.update(task) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト 更新対象が存在しない")
    fun testUpdate_NotFoundUpdateTarget() {
        // setup
        val task = TaskEntityFixtures.create()

        // execution
        val actual = catchThrowable { sut.update(task) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }
}
