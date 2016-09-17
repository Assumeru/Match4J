jQuery(function($) {
	if($EEstore.getItem('currentList', null) === null) {
		$('[data-type="has-test"]').hide();
	}
});