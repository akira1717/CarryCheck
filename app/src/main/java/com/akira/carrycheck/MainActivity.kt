package com.akira.carrycheck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.akira.carrycheck.navigation.AppNavGraph
import com.akira.carrycheck.ui.theme.CarryCheckTheme

class MainActivity : ComponentActivity() {

    // 音声認識権限の要求ランチャー
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 権限が許可された場合の処理
        } else {
            // 権限が拒否された場合の処理（手入力モードに切り替え）
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 音声認識権限をチェック
        checkAudioPermission()

        setContent {
            CarryCheckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
                }
            }
        }
    }

    /**
     * 音声認識権限をチェックして必要に応じて要求
     */
    private fun checkAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 権限が既に許可されている
            }
            else -> {
                // 権限を要求
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
