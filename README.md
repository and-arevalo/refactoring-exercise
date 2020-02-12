# Code Review / Refactoring exercise

## Question 1

I reviewed the original code and added comments to each line that contains code smells, bad development practices, and possible runtime errors that would occur.

## Question 2
I created an interface LogHandler, which only has one method for logging messages. This way, all possible handlers should implement this new interface, so implemented clients can depend on abstraction, instead of relying on concrete implementations. A LogHandler can be anything, i.e., a Console logger, a File Logger, a Database Logger, a JMS Logger, a Webservice Logger, among others.

Furthermore, I created an interface named JobLogger, which offers methods for setting the logging level, logging messages, and adding or removing log handlers at runtime. As a result, a JobLogger is only a collection of handlers. Therefore, the task of logging will consist only of iterating all the associated handlers and requesting the log messages to each handler. A JobLogger can also have many handlers of the same type with different parameters.

Finally, I developed several unit tests with JUnit, Mockito, and PowerMock.