
package ke.co.kimtech.mobiledefender

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit

class HealthWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        return try {
            val token = SecureTokenStore.read(
                applicationContext
            ) ?: return Result.success()

            val response = ApiClient.verifySession(token)

            if (!response.optBoolean("success", false)) {
                SecureTokenStore.clear(applicationContext)
                return Result.success()
            }

            // Perform a local security assessment.
            // Findings are currently kept locally and are
            // not uploaded to the server.
            AppRiskScanner.scan(applicationContext)

            Result.success()

        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {

        private const val WORK_NAME =
            "kimtech_mobile_health_monitor"

        fun schedule(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()

            val request =
                PeriodicWorkRequestBuilder<HealthWorker>(
                    15,
                    TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
        }
    }
}
