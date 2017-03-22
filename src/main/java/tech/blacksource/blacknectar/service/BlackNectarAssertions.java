/*
 * Copyright 2017 BlackSource, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package tech.blacksource.blacknectar.service;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.sirwellington.alchemy.annotations.access.NonInstantiable;
import tech.sirwellington.alchemy.arguments.AlchemyAssertion;

import static tech.sirwellington.alchemy.arguments.Arguments.checkThat;
import static tech.sirwellington.alchemy.arguments.assertions.StringAssertions.stringWithLengthLessThanOrEqualTo;

/**
 *
 * @author SirWellington
 */
@NonInstantiable
public final class BlackNectarAssertions 
{
    private final static Logger LOG = LoggerFactory.getLogger(BlackNectarAssertions.class);
    
    /**
     * This is the maximum length of any query parameter or value that can be 
     * passed into the Service. Any String longer than this has to be suspicious.
     */
    public static final int MAX_QUERY_PARAMETER_ARGUMENT_LENGTH = 1_000;

    
    private BlackNectarAssertions() throws IllegalAccessException
    {
        throw new IllegalAccessException("cannot instantiate");
    }
    
    /**
     * Checks that an argument String does not exceed {@link #MAX_QUERY_PARAMETER_ARGUMENT_LENGTH}.
     * 
     * @return 
     */
    public static AlchemyAssertion<String> argumentWithSaneLength()
    {
        return arg ->
        {
            if (Strings.isNullOrEmpty(arg))
            {
                return;
            }
            
            checkThat(arg)
                .usingMessage("Argument is too long at " + arg.length() + " characters")
                .is(stringWithLengthLessThanOrEqualTo(MAX_QUERY_PARAMETER_ARGUMENT_LENGTH));
        };
    }
}