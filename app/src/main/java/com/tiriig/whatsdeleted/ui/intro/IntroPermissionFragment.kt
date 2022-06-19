package com.tiriig.whatsdeleted.ui.intro

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import com.tiriig.whatsdeleted.databinding.FragmentIntroPermissionBinding
import com.tiriig.whatsdeleted.utility.hide
import com.tiriig.whatsdeleted.utility.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroPermissionFragment : Fragment() , SlidePolicy {

    private var _binding: FragmentIntroPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enableBtn.isVisible = !isNotificationServiceEnabled()
        binding.enableBtn.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    override val isPolicyRespected: Boolean
        get() = isNotificationServiceEnabled()

    override fun onUserIllegallyRequestedNextPage() {
        toast("Please enable notification permission")
    }

    override fun onResume() {
        super.onResume()
        if (isPolicyRespected) binding.enableBtn.hide()
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = requireActivity().packageName
        val flat: String? = Settings.Secure.getString(
            requireActivity().contentResolver,
            "enabled_notification_listeners"
        )
        flat?.let {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}