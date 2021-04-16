package com.example.grosure.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class OnboardingActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
                AppIntroFragment.newInstance(
                        title = "Welcome to",
                        description = "Art Science Museum Star Wars Night",
                        backgroundColor = Color.parseColor("#335f87")

                ))
        addSlide(AppIntroFragment.newInstance(
                title = "Enjoy your Favourite Films",
                description = "– The Force will be strong in Singapore as ArtScience Museum and Pico Pro combine their powers to host STAR WARS Identities from 30 January to 13\n" +
                        "June 2021. This marks the final stop of the exhibition’s global tour.",
//            backgroundDrawable = R.drawable.the_background_image,
                titleColor = Color.YELLOW,
//            descriptionColor = Color.RED,
                backgroundColor = Color.parseColor("#6fbdd1")
//            titleTypefaceFontRes = R.font.opensans_regular,
//            descriptionTypefaceFontRes = R.font.opensans_regular,
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