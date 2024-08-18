import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CameraCapture(onImageCaptured: (Uri?) -> Unit) {
    val context = LocalContext.current

    // State for the photo URI and capture status
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isPhotoCaptured by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            isPhotoCaptured = success
            if (success) {
                Toast.makeText(context, "Photo captured: $photoUri", Toast.LENGTH_LONG).show()
                onImageCaptured(photoUri)
            } else {
                Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_LONG).show()
                onImageCaptured(null)
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                try {
                    val photoFile = createImageFile(context)
                    photoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                    isPhotoCaptured = false
                    photoUri?.let {
                        cameraLauncher.launch(it)
                    } ?: run {
                        Toast.makeText(context, "Error: photoUri is null", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error creating file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    )


    Column() {
        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Cyan,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "Scatta una foto",
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun createImageFile(context: android.content.Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}
