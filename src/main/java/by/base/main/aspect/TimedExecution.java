package by.base.main.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация используется для контроля времени выполнения отдельных методов
 * 
 * @author Dima Hrushevski
 */
@Target(ElementType.METHOD) // Указывает, что аннотация применяется только к методам
@Retention(RetentionPolicy.RUNTIME) // Аннотация будет доступна во время выполнения
public @interface TimedExecution {

}
