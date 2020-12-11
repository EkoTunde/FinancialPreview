package com.ekosoftware.financialpreview.presentation.ui.movement

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.ekosoftware.financialpreview.R

class HistoricMovementFragmentDirections private constructor() {
  companion object {
    fun actionReSchedule(): NavDirections = ActionOnlyNavDirections(R.id.action_re_schedule)
  }
}
