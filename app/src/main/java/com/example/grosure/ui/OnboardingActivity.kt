package com.example.grosure.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.grosure.R
import com.github.appintro.AppIntro
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment

class OnboardingActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
                AppIntroFragment.newInstance(
                        title = "This is GroSure+",
                        description = "Your one-stop app to help better manage your groceries and financials.",
                        backgroundDrawable = R.drawable.onboarding_slide1,
                        imageDrawable = R.drawable.grocery_1

                ))
        addSlide(
                AppIntroFragment.newInstance(
                        title = "Plan and manage your purchases",
                        description = "Better keep track of your money and purchases, and gain more control of your financials.",
                        backgroundDrawable = R.drawable.onboarding_slide2,
                        imageDrawable = R.drawable.grocery_2

                ))
        addSlide(
                AppIntroFragment.newInstance(
                        title = "Gain insights into your spending habits",
                        description = "GroSure tracks your past purchases and trips, and gives you insights and information about your spending habits.",
                        backgroundDrawable = R.drawable.onboarding_slide3,
                        imageDrawable = R.drawable.grocery_3

                ))
        addSlide(
                AppIntroFragment.newInstance(
                        title = "Store Locator",
                        description = "See and find stores which are near you, speeding up the time you take to travel to get groceries.",
                        backgroundDrawable = R.drawable.onboarding_slide4,
                        imageDrawable = R.drawable.grocery_4

                ))
        addSlide(
                AppIntroFragment.newInstance(
                        title = "Notifications",
                        description = "Get notifications on shopping day, so you know when to get ready.",
                        backgroundDrawable = R.drawable.onboarding_slide5,
                        imageDrawable = R.drawable.grocery_5

                ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }
}