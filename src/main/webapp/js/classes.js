Match.Test = function() {
	this.repeat = false;
	this.words = [];
	this.results = {};
	this.current = null;
	this.title = '';
	this.id = 0;
	this.caseSensitive = true;
	this.password = false;
};
Match.Test.prototype.fix = function() {
	var $key, $n,
	$cache = {};
	for($n = 0; $n < this.words.length; $n++) {
		$key = this.words[$n].id;
		if($cache[$key] === undefined) {
			$cache[$key] = {
				id: $key,
				word: this.words[$n].word,
				matches: this.words[$n].matches,
				tries: this.words[$n].tries !== undefined ? this.words[$n].tries : 1
			};
		}
		this.words[$n] = $cache[$key];
	}
	if(this.current !== null) {
		$key = this.current.id;
		if($cache[$key] !== undefined) {
			this.current = $cache[$key];
		}
	}
};
Match.Test.prototype.next = function() {
	this.current = this.words.pop();
};
Match.Test.prototype.answerQuestion = function($answer) {
	var $this = this;
	function checkCase() {
		var $n;
		if(!$this.caseSensitive) {
			$answer = $answer.toUpperCase();
			for($n = 0; $n < $this.current.matches.length; $n++) {
				if($answer == $this.current.matches[$n].toUpperCase()) {
					return true;
				}
			}
		}
		return false;
	}

	if(this.current.matches.indexOf($answer) !== -1 || checkCase()) {
		this.addResult(this.current.tries, 'right');
		return true;
	} else {
		this.addResult(this.current.tries, 'wrong');
		if(this.repeat) {
			this.current.tries++;
			this.words.unshift(this.current);
			this.words.splice(
				Math.min(3, this.words.length),
				0,
				this.current
			);
		}
		return false;
	}
};
Match.Test.prototype.addResult = function($tries, $type) {
	if(this.results[$tries] === undefined) {
		this.results[$tries] = {
			right: 0,
			wrong: 0
		};
	}
	this.results[$tries][$type]++;
};