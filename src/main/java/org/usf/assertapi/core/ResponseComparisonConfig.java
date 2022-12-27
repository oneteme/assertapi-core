package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type")
	@JsonSubTypes({
	    @Type(value = JsonResponseComparisonConfig.class, name = "JSON") })
public interface ResponseComparisonConfig {
	
	ResponseType getType();
	
}
