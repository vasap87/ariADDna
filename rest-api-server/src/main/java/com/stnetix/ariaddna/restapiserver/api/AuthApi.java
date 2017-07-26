/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.stnetix.ariaddna.restapiserver.api;

import com.stnetix.ariaddna.restapiserver.model.Credential;
import com.stnetix.ariaddna.restapiserver.model.ErrorModel;
import com.stnetix.ariaddna.restapiserver.model.Session;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-24T10:27:06.657+03:00")

@Api(value = "auth", description = "the auth API")
public interface AuthApi {

    @ApiOperation(value = "", notes = "Creating new user session.", response = Session.class, tags={ "Ariaddna", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "User session.", response = Session.class),
        @ApiResponse(code = 409, message = "User not found on server.", response = ErrorModel.class),
        @ApiResponse(code = 200, message = "Unexpected error.", response = ErrorModel.class) })
    
    @RequestMapping(value = "/auth",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<Session> authUser(@ApiParam(value = "Authorization user credential." ,required=true )  @Valid @RequestBody Credential user);


    @ApiOperation(value = "", notes = "Closing user session.", response = Void.class, tags={ "Ariaddna", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Sesson closed.", response = Void.class),
        @ApiResponse(code = 404, message = "Session not found.", response = ErrorModel.class),
        @ApiResponse(code = 200, message = "Unexpected error.", response = ErrorModel.class) })
    
    @RequestMapping(value = "/auth/{uuid}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<Void> logoutSession(@ApiParam(value = "UUID of user session.",required=true ) @PathVariable("uuid") String uuid);

}