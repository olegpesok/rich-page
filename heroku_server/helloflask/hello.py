import os
from flask import Flask, request
import nltk
import json

nltk.download('maxent_treebank_pos_tagger')
nltk.download('maxent_ne_chunker')
nltk.download('words')

app = Flask(__name__)


def pos(text):
	return nltk.pos_tag(nltk.word_tokenize(text))

def names(text):
	part_of_speech = pos(text)
	return [w for w in part_of_speech if w[1] in ('NN','NNP','NNS')]

def names2(text):
	part_of_speech = pos(text)
	results = []
	current_names = []
	for w in part_of_speech:
		if w[1] in ('NN','NNP','NNS'):
			current_names.append(w)
		else:
			if current_names:
				results.append(current_names)
			current_names = []
	if results and current_names and results[-1] != current_names:
		results.append(current_names)

	return results

def ne(text):
	part_of_speech = pos(text)
	return nltk.ne_chunk(part_of_speech)

def main(text):
	return json.dumps(names2(text))

@app.route('/')
def hello():
	t = request.args.get('t')
	return main(t)
	# nltk.ne_chunk(pos)
	