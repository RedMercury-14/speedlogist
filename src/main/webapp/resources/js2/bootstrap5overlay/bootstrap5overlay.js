export const bootstrap5overlay = {
    showOverlay() {
        $('body').addClass('no-scroll')
        $('#overlay').removeClass('none')
    },

    hideOverlay() {
        $('body').removeClass('no-scroll')
        $('#overlay').addClass('none')
    }
}
