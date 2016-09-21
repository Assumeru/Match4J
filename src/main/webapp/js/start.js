(function() {
	var $test;

	function shuffle($array) {
		var $n, $k, $out = [];
		for($n = 0; $n < $array.length; $n++) {
			$k = Math.floor(Math.random() * $n);
			$out[$n] = $out[$k];
			$out[$k] = $array[$n];
		}
		return $out;
	}

	if(Match.settings.current !== undefined) {
		$test = new Match.Test();
		$test.id = Match.settings.current.id;
		$test.title = Match.settings.current.title;
		$test.repeat = Match.settings.current.repeat;
		$test.caseSensitive = Match.settings.current.caseSensitive;
		$test.password = Match.settings.current.password;
		$test.words = shuffle(Match.settings.current.words);
		if(Match.settings.current.double) {
			$test.words = $test.words.concat(shuffle(Match.settings.current.words));
		}
		Match.Store.setObject('currentTest', $test);
		Match.settings.currentTest = $test;
	}
})();