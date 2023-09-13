package org.nrg.xnatx.dicomweb.xapi;

import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xnatx.dicomweb.toolkit.MediaTypes;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
public @interface QidoRequestMapping
{
	@AliasFor(annotation = RequestMapping.class, attribute = "name")
	String name() default "";

	@AliasFor(annotation = RequestMapping.class, attribute = "value")
	String[] value() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "path")
	String[] path() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "method")
	RequestMethod[] method() default RequestMethod.GET;

	@AliasFor(annotation = RequestMapping.class, attribute = "params")
	String[] params() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "headers")
	String[] headers() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "consumes")
	String[] consumes() default {};

	@AliasFor(annotation = RequestMapping.class, attribute = "produces")
	String[] produces() default {
		MediaType.APPLICATION_JSON_VALUE,
		MediaTypes.APPLICATION_DICOM_JSON_VALUE};

	AccessLevel restrictTo() default AccessLevel.Read;
}
