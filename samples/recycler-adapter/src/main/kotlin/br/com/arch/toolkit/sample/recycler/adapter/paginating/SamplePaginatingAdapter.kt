package br.com.arch.toolkit.sample.recycler.adapter.paginating

import android.content.Context
import br.com.arch.toolkit.recycler.adapter.ViewBinder
import br.com.arch.toolkit.recycler.adapter.paginating.PaginatingAdapter

class SamplePaginatingAdapter: PaginatingAdapter<String>() {

    override fun creatorView(context: Context, viewType: Int): ViewBinder<String> {
        return NumberView(context)
    }

}