jQuery(function($) {
	if($EEstore.getItem('currentTest', null) === null) {
		$('[data-type="has-test"]').hide();
	}
});