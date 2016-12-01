/*
 * Copyright 2016 BlackWholeLabs.
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

package tech.blacksource.blacknectar.service.api;

import java.sql.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * @author SirWellington
 */
@RunWith(AlchemyTestRunner.class)
public class BlackNectarServiceTest 
{
    
    @Mock
    private Connection connection;
    
    @Before
    public void setUp() throws Exception
    {
        
        setupData();
        setupMocks();
    }


    private void setupData() throws Exception
    {
        
    }

    private void setupMocks() throws Exception
    {
        
    }
    @Test
    public void testNewMemoryService()
    {
        BlackNectarService instance = BlackNectarService.newMemoryService();
        assertThat(instance, notNullValue());
    }

    @Test
    public void testNewSQLService() throws Exception
    {
        BlackNectarService result = BlackNectarService.newSQLService(connection);
        assertThat(result, notNullValue());
    }
}