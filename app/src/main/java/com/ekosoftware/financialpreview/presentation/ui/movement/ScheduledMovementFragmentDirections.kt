package com.ekosoftware.financialpreview.presentation.ui.movement

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.ekosoftware.financialpreview.R

class ScheduledMovementFragmentDirections private constructor() {
  companion object {
    fun actionMove(): NavDirections = ActionOnlyNavDirections(R.id.action_move)
  }
}
