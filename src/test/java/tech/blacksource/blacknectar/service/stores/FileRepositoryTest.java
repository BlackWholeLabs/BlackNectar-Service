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

package tech.blacksource.blacknectar.service.stores;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import tech.aroma.client.Aroma;
import tech.sirwellington.alchemy.test.junit.runners.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Mockito.*;
import static tech.blacksource.blacknectar.service.BlackNectarGenerators.stores;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.CollectionGenerators.listOf;
import static tech.sirwellington.alchemy.generator.NumberGenerators.doubles;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphabeticString;
import static tech.sirwellington.alchemy.test.junit.runners.GenerateString.Type.UUID;


/**
 * @author SirWellington
 */
@Repeat(10)
@RunWith(AlchemyTestRunner.class)
public class FileRepositoryTest
{

    @Mock(answer = RETURNS_MOCKS)
    private Aroma aroma;

    @Mock
    private IDGenerator idGenerator;

    private FileStoreDataSource instance;

    private Store store;

    @GenerateString(UUID)
    private String storeIdString;

    private UUID storeId;

    @Before
    public void setUp() throws Exception
    {

        setupData();
        setupMocks();

        instance = new FileStoreDataSource(aroma, idGenerator);
    }


    private void setupData() throws Exception
    {
        store = one(stores());
        storeId = java.util.UUID.fromString(storeIdString);
    }

    private void setupMocks() throws Exception
    {
        when(idGenerator.generateKey()).thenReturn(storeId);
        when(idGenerator.generateKeyAsString()).thenReturn(storeIdString);
    }

    @DontRepeat
    @Test
    public void testGetAllStores()
    {
        List<Store> results = instance.getAllStores();
        assertThat(results, notNullValue());
        assertThat(results, not(empty()));
        assertThat(results.size(), greaterThanOrEqualTo(100));
        assertThat(results.size(), lessThanOrEqualTo(FileStoreDataSource.MAXIMUM_STORES));

        results.forEach(s -> assertThat(s.getStoreId(), is(storeIdString)));
        verify(idGenerator, atLeastOnce()).generateKey();
    }

    @DontRepeat
    @Test
    public void testReadCSVFile()
    {
        String file = instance.readCSVFile();
        assertThat(file, not(isEmptyOrNullString()));
    }

    @Test
    public void testSplitFileIntoLines()
    {
        List<String> lines = listOf(alphabeticString());

        String file = String.join("\n", lines);

        List<String> result = instance.splitFileIntoLines(file);
        assertThat(result, is(lines));
    }

    @Test
    public void testExtractLocationFrom()
    {
        double latitude = one(doubles(-10, 10));
        double longitude = one(doubles(-10, 10));

        String latitudeString = String.valueOf(latitude);
        String longitudeString = String.valueOf(longitude);

        Location result = instance.extractLocationFrom(latitudeString, longitudeString);
        assertThat(result.getLatitude(), is(latitude));
        assertThat(result.getLongitude(), is(longitude));
    }

}