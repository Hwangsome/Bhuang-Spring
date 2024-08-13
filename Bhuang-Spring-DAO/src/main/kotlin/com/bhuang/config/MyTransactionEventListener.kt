package com.bhuang.config

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MyTransactionEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleTransactionAfterCommit(event: Any) {
        println("Handling event after transaction commit: $event")
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleTransactionBeforeCommit(event: Any) {
        println("Handling event before transaction commit: $event")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handleTransactionAfterRollback(event: Any) {
        println("Handling event after transaction rollback: $event")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    fun handleTransactionAfterCompletion(event: Any) {
        println("Handling event after transaction completion: $event")
    }
}
