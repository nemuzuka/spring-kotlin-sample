package net.jp.vss.sample.controller.tasks

import net.jp.vss.sample.controller.ListResponse
import net.jp.vss.sample.usecase.tasks.ListTaskUseCase
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * ListTask の APIController.
 *
 * @property listTaskUseCase ListTask の UseCase
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
class ListTaskApiController(
    private val listTaskUseCase: ListTaskUseCase
) {
    /**
     * ListTask.
     *
     * @return レスポンス
     */
    @RequestMapping(method = [RequestMethod.GET], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun listTask(): ResponseEntity<ListResponse<TaskUseCaseResult>> =
        ResponseEntity.ok(ListResponse(listTaskUseCase.allTasks()))
}
