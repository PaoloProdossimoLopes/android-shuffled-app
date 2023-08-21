package com.programou.shuffled.authenticated.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


open class ViewModelTests {
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()
}

class ProfileViewModelTests: ViewModelTests() {

    @Test
    fun `on resume displays loader once`() {
        val view: ProfileViewDiplaying = mock()
        val sut = ProfileViewModel(view, mock())
        sut.onResume()
        verify(view, times(1)).displayLoader()
        verify(view, times(0)).hideLoader()
    }

    @Test
    fun `on resume should load costumer infromation once`() {
        val costumerLoader: CostumerLoader = mock()
        val sut = ProfileViewModel(mock(), costumerLoader)
        sut.onResume()
        verify(costumerLoader, times(1)).loadCostumer(any())
    }

    @Test
    fun `on resume should hides loader when load costumer completes with information`() {
        val loadCostumerCallback = argumentCaptor<(Costumer) -> Unit>()
        val costumerLoader: CostumerLoader = mock {
            on { loadCostumer(loadCostumerCallback.capture()) } doAnswer { loadCostumerCallback.firstValue(makeDummyCostumer()) }
        }
        val view: ProfileViewDiplaying = mock()
        val sut = ProfileViewModel(view, costumerLoader)
        sut.onResume()
        verify(view, times(1)).displayLoader()
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun `on resume should display costumer view data when load costumer completes`() {
        val costumer = makeDummyCostumer()
        val loadCostumerCallback = argumentCaptor<(Costumer) -> Unit>()
        val costumerLoader: CostumerLoader = mock {
            on { loadCostumer(loadCostumerCallback.capture()) } doAnswer { loadCostumerCallback.firstValue(costumer) }
        }
        val view: ProfileViewDiplaying = mock()
        val sut = ProfileViewModel(view, costumerLoader)
        sut.onResume()
        verify(view, times(1)).displayCostumer(eq(CostumerViewData(costumer)))
    }

    @Test
    fun `on resume should display alert with invalid costumer data when load costumer delievers InvalidCostumerInformationError`() {
        val costumerLoader: CostumerLoader = mock {
            on { loadCostumer(any()) } doAnswer { throw InvalidCostumerInformationError() }
        }
        val view: ProfileViewDiplaying = mock()
        val sut = ProfileViewModel(view, costumerLoader)
        sut.onResume()
        verify(view, times(1)).displayAlert(eq(ProfileAlertViewData("Ops, something wrong occurs", "try again later")))
        verify(view, times(0)).displayCostumer(any())
    }

    @Test
    fun `on logout should call logout from loout`() {
        val costumerLoader: CostumerLoader = mock()
        val view: ProfileViewDiplaying = mock()
        val sut = ProfileViewModel(view, costumerLoader)
        sut.logout()
        verify(costumerLoader, times(1)).logout()
    }
}

private fun makeDummyCostumer() = Costumer("any user name", "any user email", "https://valid-url.com")

class LoadCostumerTests {
    @Test
    fun `on load costumer should get costumer from session`() {
        val session: Session = mock {
            on { getCostumer() } doReturn (makeDummyCostumer())
        }
        val sut = LoadCostumer(session)
        sut.loadCostumer {}
        verify(session, times(1)).getCostumer()
    }

    @Test
    fun `on load costumer should deliver a costumer when is all fields correct`() {
        val costumer = makeDummyCostumer()
        val session: Session = mock {
            on { getCostumer() } doReturn (costumer)
        }
        val sut = LoadCostumer(session)
        val costumerReceiveds = mutableListOf<Costumer>()
        sut.loadCostumer { costumerReceiveds.add(it) }
        Assert.assertEquals(costumerReceiveds, listOf(costumer))
    }

    @Test
    fun `on load costumer should thow an InvalidCostumerInformationError when name is empty`() {
        val session: Session = mock {
            on { getCostumer() } doReturn (Costumer("", "any valid email", "https://valid-url.com"))
        }
        val sut = LoadCostumer(session)
        Assert.assertThrows(InvalidCostumerInformationError::class.java) { sut.loadCostumer {} }
    }

    @Test
    fun `on load costumer should throw an InvalidCostumerInformationError when email is empty`() {
        val session: Session = mock {
            on { getCostumer() } doReturn (Costumer("any valid name", "", "https://valid-url.com"))
        }
        val sut = LoadCostumer(session)
        Assert.assertThrows(InvalidCostumerInformationError::class.java) { sut.loadCostumer {} }
    }

    @Test
    fun `on logout costumer should call logout from session once`() {
        val session: Session = mock {
            on { getCostumer() } doReturn (Costumer("any valid name", "", "https://valid-url.com"))
        }
        val sut = LoadCostumer(session)
        sut.logout()
        verify(session, times(1)).logout()
    }
}