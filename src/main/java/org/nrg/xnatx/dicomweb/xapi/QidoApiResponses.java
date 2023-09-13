package org.nrg.xnatx.dicomweb.xapi;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Search completed successfully."),
		@ApiResponse(code = 204, message = "Search completed successfully, but there were no results."),
		@ApiResponse(code = 400, message = "There was a problem with the request."),
		@ApiResponse(code = 403, message = "The user does not have the right permission."),
		@ApiResponse(code = 500, message = "An unexpected error occurred.")
	})
public @interface QidoApiResponses
{
}
