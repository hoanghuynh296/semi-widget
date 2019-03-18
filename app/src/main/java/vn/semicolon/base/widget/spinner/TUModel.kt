package vn.semicolon.base.widget.spinner

data class TUModel(val title: String): PopUpModel {
    override fun getDisplay(): String {
        return title
    }

}