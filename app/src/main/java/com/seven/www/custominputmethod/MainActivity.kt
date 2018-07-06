package com.seven.www.custominputmethod

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomKeyButton.OnPressedListener {

    companion object {
        const private val INVALID_TONE_DTMF = -1
    }

    private val TAG = "MainActivity"


    lateinit var etBinaryInput: EditText
    lateinit var btnZero: CustomKeyButton
    lateinit var btnOne: CustomKeyButton

    private var toneGenerator: ToneGenerator? = null
    private val toneGeneratorLock: Any = Any()

    private val pressedKeyButtons: HashSet<View> = HashSet(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBinaryInput = findViewById(R.id.etBinaryInput)
        btnZero = findViewById(R.id.btnZero)
        btnOne = findViewById(R.id.btnOne)

        btnZero.setOnPressedListener(this)
        btnOne.setOnPressedListener(this)
        // generate by kotlin plugin, don't need to call findViewById
        btnDel.setOnLongClickListener { _ -> etBinaryInput.setText(""); true }
        btnDel.setOnClickListener{ _ -> handlePressedEvent(KeyEvent.KEYCODE_DEL, INVALID_TONE_DTMF)}
    }

    override fun onStart() {
        super.onStart()

        // Get toneGenerator
        synchronized(toneGeneratorLock) {
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_DTMF, 80)
            } catch (e: RuntimeException) {
                null
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // Release toneGenerator
        synchronized(toneGeneratorLock) {
            toneGenerator?.release()
            toneGenerator = null
        }
    }

    private fun shouldPlayTone(): Boolean {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ringerMode = audioManager.ringerMode
        return (ringerMode != AudioManager.RINGER_MODE_SILENT) && (ringerMode != AudioManager.RINGER_MODE_VIBRATE)
    }

    private fun playTone(tone: Int, durationMs: Int) {

        if (!shouldPlayTone() || (tone == INVALID_TONE_DTMF)) {
            return
        }

        synchronized(toneGeneratorLock) {
            toneGenerator?.startTone(tone, durationMs)
        }
    }

    private fun stopTone() {
        synchronized(toneGeneratorLock) {
            toneGenerator?.stopTone()
        }
    }

    override fun onPressed(view: View, pressed: Boolean) {

        if (pressed) {

            when (view.id) {
                R.id.btnZero -> handlePressedEvent(KeyEvent.KEYCODE_0, ToneGenerator.TONE_DTMF_0)
                R.id.btnOne -> handlePressedEvent(KeyEvent.KEYCODE_1, ToneGenerator.TONE_DTMF_1)
                else ->  Log.wtf(TAG, "Unexpected pressed event from view: $view")
            }

            pressedKeyButtons.add(view)
        } else {

            pressedKeyButtons.remove(view)
            if (pressedKeyButtons.isEmpty()) {
                stopTone()
            }
        }
    }

    private fun handlePressedEvent(keyCode: Int, keyTone: Int) {
        playTone(keyTone, - 1)
        val kv = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        etBinaryInput.onKeyDown(keyCode, kv)
    }
}
