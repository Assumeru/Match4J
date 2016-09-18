jQuery(function($) {
	$('#main [data-type="clear"]').click(function() {
		$('#main [data-type="words"]').empty();
	});
	$('#main [data-type="add"]').click(function() {
		var $first, $row, $n,
		$words = $('#main [data-type="words"]');
		for($n = 0; $n < 5; $n++) {
			$row = $('#template-input-row tr').clone();
			$words.append($row);
			if($first === undefined) {
				$first = $row.find('input[name="first_word[]"]');
			}
		}
		$first.focus();
	});
});