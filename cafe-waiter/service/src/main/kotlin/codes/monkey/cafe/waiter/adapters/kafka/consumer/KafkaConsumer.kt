package codes.monkey.cafe.waiter.adapters.kafka.consumer

import codes.monkey.cafe.kitchen.events.OrderPreparationCompletedEvent
import codes.monkey.cafe.stockroom.events.OutOfStockEvent
import codes.monkey.cafe.stockroom.events.StockroomReStockedEvent
import codes.monkey.cafe.waiter.domain.model.DeliverOrderToTableCommand
import codes.monkey.cafe.waiter.domain.model.StartTakingOrdersForFreshlyStockedItemsCommand
import codes.monkey.cafe.waiter.domain.model.StopTakingOrdersForOutOfStockItemsCommand
import codes.monkey.cafe.waiter.domain.model.WaiterConstants
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaConsumer(
        val commandGateway: CommandGateway) {

    @KafkaListener(topics = ["cafe.kitchen.events"])
    fun handleKitchenEvents(event: SpecificRecordBase) {
        when (event) {
            is OrderPreparationCompletedEvent -> commandGateway.sendAndWait<Any>(DeliverOrderToTableCommand(
                    orderId = UUID.fromString(event.orderId.toString()),
                    waiterId = UUID.fromString(event.waiterId.toString())))
        }
    }

    @KafkaListener(topics = ["cafe.stockroom.events"])
    fun handleStockroomEvents(event: SpecificRecordBase) {
        when (event) {
            is OutOfStockEvent -> commandGateway.sendAndWait<Any>(
                    StopTakingOrdersForOutOfStockItemsCommand(
                            waiterId = WaiterConstants.WAITER_ID,
                            items = event.stock.map { it.toString() }))
            is StockroomReStockedEvent -> commandGateway.sendAndWait<Any>(
                    StartTakingOrdersForFreshlyStockedItemsCommand(
                            waiterId = WaiterConstants.WAITER_ID,
                            items = event.newStock.map { it.toString() }
                    )
            )
        }
    }

}