(function($) {
	var $current;

	function initTest() {
		var $n, $key,
		$keys = ['answer', 'caseSensitive', 'current', 'id', 'question', 'repeat', 'results', 'title', 'words', 'password'],
		$test = Match.settings.currentTest !== undefined ? Match.settings.currentTest : Match.Store.getObject('currentTest', null);
		if($test !== null && $test.words !== undefined && $test.words.length > 0) {
			$current = new Match.Test();
			for($n = 0; $n < $keys.length; $n++) {
				$key = $keys[$n];
				$current[$key] = $test[$key];
			}
			$current.fix();
		} else {
			window.location = './';
		}
	}

	function start() {
		$('[data-result]').hide();
		$('#current form').submit(submitAnswer);
		$('#current [data-type="title"]').text($current.title);
		if($current.current === null) {
			$current.next();
		}
		$('#current > .loading').hide();
		$('#current > .hidden').removeClass('hidden');
		showQuestion();
	}

	function showQuestion() {
		var $input = $('#current [name="answer"]');
		fillResultsTable('current');
		$('#current [data-type="current"]').text($current.current.word);
		$('#current [data-type="number"]').text($current.words.length);
		$input.val('');
		$input.focus();
	}

	function fillResultsTable($id) {
		var $key,
		$results = $('#'+$id+' [data-type="results"]');
		$results.empty();
		for($key in $current.results) {
			$results.append('<tr><td>'+$key+'</td><td>'+$current.results[$key].right+'</td><td>'+$current.results[$key].wrong+'</td></tr>');
		}
	}

	function submitAnswer($e) {
		var $answer = $('#current [name="answer"]').val().trim(),
		$result = $current.answerQuestion($answer);
		$e.preventDefault();
		if($result) {
			$('#current [data-result="correct"]').show();
			$('#current [data-result="wrong"]').hide();
		} else {
			$('#current [data-result="wrong"]').show();
			$('#current [data-result="correct"]').hide();
			$('#current [data-type="correct"]').text($current.current.matches.join(' or '));
			$('#current [data-type="not-wrong"]').show().off().click(bindNotWrong($current.current, $answer));
		}
		if($current.words.length > 0) {
			$current.next();
			showQuestion();
			Match.Store.setObject('currentTest', $current);
		} else {
			Match.Store.removeItem('currentTest');
			showResults();
		}
	}

	function bindNotWrong($word, $answer) {
		return function($e) {
			$e.preventDefault();
			if(window.confirm('Are you sure you want to add "' + $answer + '" as a match for "' + $word.word + '"?')) {
				var $password;
				if($current.password) {
					$password = window.prompt('This list requires a password to edit');
				}
				$.ajax({
					method: 'POST',
					url: Match.settings.ajaxurl,
					data: {
						type: 'add-match',
						list: $current.id,
						word: $word.id,
						match: $answer,
						password: $password
					}
				}).done(function($msg) {
					$word.matches.push($answer);
					console.log($msg);
				}).fail(function($msg) {
					var $error = 'Something went wrong adding "' + $answer + '" as a match for "' + $word.word + '"';
					if($msg.responseJSON && $msg.responseJSON.error !== undefined) {
						$error += ':\n' + $msg.responseJSON.error;
					}
					window.alert($error);
				});
				$(this).hide();
			}
		}
	}

	function showResults() {
		var $n, $grade,
		$right = 0,
		$wrong = 0;
		for($n in $current.results) {
			$right += $current.results[$n].right;
			$wrong += $current.results[$n].wrong;
		}
		$grade = Math.round(($right * 900 / ($right + $wrong)) + 100) / 100;
		$('#results [data-type="grade"]').text($grade);
		fillResultsTable('results');
		$('#results').removeClass('hidden');
		$current = undefined;
		$('#current').hide();
	}

	initTest();
	$(start);
})(jQuery);