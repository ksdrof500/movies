package br.com.movies_tek.ui.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class BaseBindingViewHolder<out T : ViewDataBinding>(
        val binding: T
) : RecyclerView.ViewHolder(binding.root)
