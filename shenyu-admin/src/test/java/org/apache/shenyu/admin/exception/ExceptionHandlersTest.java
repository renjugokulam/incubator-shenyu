/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shenyu.admin.exception;

import org.apache.shenyu.admin.model.result.ShenyuAdminResult;
import org.apache.shenyu.admin.utils.ShenyuResultMessage;
import org.apache.shenyu.common.exception.CommonErrorCode;
import org.apache.shenyu.common.exception.ShenyuException;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test case for {@link ExceptionHandlers}.
 */
public final class ExceptionHandlersTest {

    private ExceptionHandlers exceptionHandlersUnderTest;

    @Before
    public void setUp() {
        exceptionHandlersUnderTest = new ExceptionHandlers();
    }

    @Test
    public void testServerExceptionHandlerByException() {
        Exception exception = new Exception();
        ShenyuAdminResult result = exceptionHandlersUnderTest.serverExceptionHandler(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertEquals(result.getMessage(), "The system is busy, please try again later");
    }

    @Test
    public void testServerExceptionHandlerByShenyuException() {
        Exception shenyuException = new ShenyuException("Test shenyuException message!");
        ShenyuAdminResult result = exceptionHandlersUnderTest.serverExceptionHandler(shenyuException);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertEquals(result.getMessage(), shenyuException.getMessage());
    }

    @Test
    public void testServerExceptionHandlerByDuplicateKeyException() {
        DuplicateKeyException duplicateKeyException = new DuplicateKeyException("Test duplicateKeyException message!");
        ShenyuAdminResult result = exceptionHandlersUnderTest.serverExceptionHandler(duplicateKeyException);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertEquals(result.getMessage(), ShenyuResultMessage.UNIQUE_INDEX_CONFLICT_ERROR);
    }

    @Test
    public void testShiroExceptionHandler() {
        UnauthorizedException unauthorizedException = mock(UnauthorizedException.class);
        ShenyuAdminResult result = exceptionHandlersUnderTest.shiroExceptionHandler(unauthorizedException);
        assertEquals(result.getCode().intValue(), CommonErrorCode.TOKEN_NO_PERMISSION);
        assertEquals(result.getMessage(), ShenyuResultMessage.TOKEN_HAS_NO_PERMISSION);
    }

    @Test
    public void testNullPointExceptionHandler() {
        NullPointerException nullPointerException = mock(NullPointerException.class);
        ShenyuAdminResult result = exceptionHandlersUnderTest.nullPointExceptionHandler(nullPointerException);
        assertEquals(result.getCode().intValue(), CommonErrorCode.NOT_FOUND_EXCEPTION);
        assertEquals(result.getMessage(), ShenyuResultMessage.NOT_FOUND_EXCEPTION);
    }

    @Test
    public void testHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);
        ShenyuAdminResult result = exceptionHandlersUnderTest.handleHttpRequestMethodNotSupportedException(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertThat(result.getMessage(), containsString("method is not supported for this request. Supported methods are"));
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        List<FieldError> fieldErrors = mock(List.class);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        ShenyuAdminResult result = exceptionHandlersUnderTest.handleMethodArgumentNotValidException(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertThat(result.getMessage(), containsString("Request error! invalid argument"));
    }

    @Test
    public void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException exception = mock(MissingServletRequestParameterException.class);
        ShenyuAdminResult result = exceptionHandlersUnderTest.handleMissingServletRequestParameterException(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertThat(result.getMessage(), containsString("parameter is missing"));
    }

    @Test
    public void testHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        Class clazz = MethodArgumentTypeMismatchException.class;
        when(exception.getRequiredType()).thenReturn(clazz);
        ShenyuAdminResult result = exceptionHandlersUnderTest.handleMethodArgumentTypeMismatchException(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
        assertThat(result.getMessage(), containsString("should be of type"));
    }

    @Test
    public void testHandleConstraintViolationException() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = mock(Set.class);
        when(exception.getConstraintViolations()).thenReturn(violations);
        ShenyuAdminResult result = exceptionHandlersUnderTest.handleConstraintViolationException(exception);
        assertEquals(result.getCode().intValue(), CommonErrorCode.ERROR);
    }
}
