jQuery(function($) {
	if(Match.Store.getItem('currentTest', null) === null) {
		$('[data-type="has-test"]').hide();
	}
});