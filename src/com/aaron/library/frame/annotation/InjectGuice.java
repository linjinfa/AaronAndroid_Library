package com.aaron.library.frame.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author linjinfa@126.com
 * @date 2013-5-23 上午11:16:56 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectGuice {

	/**
	 * Dao对应的PoJo的Class
	 * @return
	 */
	Class<?> daoCls() default Object.class;
	
}
