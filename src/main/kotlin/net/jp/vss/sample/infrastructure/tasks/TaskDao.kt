package net.jp.vss.sample.infrastructure.tasks

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.springframework.dao.DuplicateKeyException
import org.seasar.doma.jdbc.Result
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.dao.OptimisticLockingFailureException

/**
 * Task Dao.
 */
@Dao
@ConfigAutowireable
interface TaskDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: TaskEntity): Result<TaskEntity>

    /**
     * task_code による取得.
     *
     * @param taskCode タスクコード
     * @param options オプション(FOR UPDATE等)
     * @return 該当データ(存在しない場合、null)
     */
    @Sql("""
        SELECT *
        FROM
            tasks
        WHERE
            task_code = /*taskCode*/'abc'
    """)
    @Select
    fun findByTaskCode(taskCode: String, options: SelectOptions): TaskEntity?

    /**
     * 更新.
     *
     * @param entity 更新Entity
     * @return 結果
     * @throws OptimisticLockingFailureException 更新対象レコードが存在しない
     */
    @Update(include = ["status", "title", "contentValue", "deadline",
        "attributes", "lastUpdateUserCode", "lastUpdateAt", "versionNo"])
    fun update(entity: TaskEntity): Result<TaskEntity>

    /**
     * 全件取得.
     *
     * @return 該当レコード
     */
    @Sql("SELECT * FROM tasks ORDER BY task_id")
    @Select
    fun findAll(): List<TaskEntity>
}
