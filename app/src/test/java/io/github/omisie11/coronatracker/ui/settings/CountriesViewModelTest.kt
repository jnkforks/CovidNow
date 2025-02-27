package io.github.omisie11.coronatracker.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.github.omisie11.coronatracker.data.repository.CountriesRepository
import io.github.omisie11.coronatracker.utils.getValue
import io.github.omisie11.coronatracker.utils.testCountry1
import io.github.omisie11.coronatracker.utils.testCountry2
import io.github.omisie11.coronatracker.utils.testCountry3
import io.github.omisie11.coronatracker.utils.testCountry4
import io.github.omisie11.coronatracker.utils.testCountry5
import io.github.omisie11.coronatracker.utils.testCountry6
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CountriesViewModelTest {

    private val testCountriesList = listOf<String>(
        testCountry1.name,
        testCountry2.name,
        testCountry3.name,
        testCountry4.name,
        testCountry5.name,
        testCountry6.name
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var repository: CountriesRepository

    // class under test
    private lateinit var countriesViewModel: CountriesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getCountries() {
        val countriesFlow = flowOf(testCountriesList)
        Mockito.`when`(repository.getCountriesNamesFlow()).thenAnswer {
            return@thenAnswer countriesFlow
        }
        Mockito.`when`(repository.getFetchingStatus()).thenAnswer {
            return@thenAnswer MutableStateFlow(false)
        }
        countriesViewModel = CountriesViewModel(repository)
        val result: List<String> = getValue(countriesViewModel.getCountries())

        assertEquals(testCountriesList, result)
    }

    @Test
    fun refreshCountries_verifyCalls() = runBlocking {
        val countriesFlow = flowOf(testCountriesList)
        Mockito.`when`(repository.getCountriesNamesFlow()).thenAnswer {
            return@thenAnswer countriesFlow
        }
        Mockito.`when`(repository.getFetchingStatus()).thenAnswer {
            return@thenAnswer MutableStateFlow(false)
        }
        countriesViewModel = CountriesViewModel(repository)
        countriesViewModel.refreshCountriesData(forceRefresh = true)

        verify(repository, times(1)).refreshData(Mockito.eq(true))
    }
}
