Check-list
=

PublisherTests
-

 - [x] shouldReceiveMessage
  (Verify that consumer receive sent message)
 - [x] shouldReceiveMessageWithIntervalPositive (Verify that consumer receive messages sent by schedule with a reasonable timeout)
 - [x] shouldReceiveMessageWithIntervalNegative (Verify that consumer doesn't receive messages sent by schedule in case of too long delay)
 - [x] shouldReceiveMessageInSecondConsumer (Verify that both consumer receive the message)
 
 
 RPCProcessingTimeTests
 -
 
 - [x] shouldReceiveResponseWithReasonableTimeoutPositive (Verify that call is processed with a reasonable timeout)
 - [x] shouldReceiveResponseWithReasonableTimeoutNegative (Verify that call isn't processed in case of too long delay)
 
 
 RPCTests
 -
 
 - [x] shouldReceiveMessageInUpperCase (Verify that call processed properly)
 - [x] shouldReceiveProperMessageInCaseOfMultipleCall (Verify that response corresponds to call in case of multiple calls)
 - [x] shouldReceiveMessageInCaseMultipleServers (Verify that both consumer receive calls)