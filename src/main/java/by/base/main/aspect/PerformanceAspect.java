package by.base.main.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceAspect {

//    @Around("execution(* by.base.main.controller..*(..))") // Перехват всех методов в пакете
    @Around("@annotation(by.base.main.aspect.TimedExecution)") // Указываем аннотацию для перехвата
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Выполнение перехваченного метода
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        System.out.println("Метод " + joinPoint.getSignature() + " выполнен за " + duration + " мс");

        return result;
    }
}
