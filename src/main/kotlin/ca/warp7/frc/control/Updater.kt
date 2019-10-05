package ca.warp7.frc.control

fun updateButtonState(old: ButtonState, _new: Boolean) =
        if (_new)
            if (old == ButtonState.Pressed || old == ButtonState.HeldDown)
                ButtonState.HeldDown
            else
                ButtonState.Pressed
        else
            if (old == ButtonState.Released || old == ButtonState.None)
                ButtonState.None
            else
                ButtonState.Released