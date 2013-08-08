import os
from flask import Flask, request
import nltk
import json
import urllib
import re
import BeautifulSoup

nltk.download('maxent_treebank_pos_tagger')
nltk.download('maxent_ne_chunker')
nltk.download('words')
nltk.download('stopwords')
nltk.download('brown')

fdist = nltk.FreqDist([w.lower() for w in nltk.corpus.brown.words()]) 
stop_words = nltk.corpus.stopwords.words('english')

app = Flask(__name__)
app.config.from_pyfile('config.py')


def getFreq(name):
	nltk.download(name)
	return nltk.FreqDist([w.lower() for w in getattr(nltk.corpus,name).words()]) 

def visible(element):
    if element.parent.name in ['style', 'script', '[document]', 'head', 'title']:
        return False
    elif re.match('<!--.*-->', str(element)):
        return False
    return True

def clean_with_bs(url):
	html = urllib.urlopen(url).read()
	soup = BeautifulSoup.BeautifulSoup(html)
	texts = soup.findAll(text=True)
	results = filter(visible, texts)
	results = [x.strip('\n\t\r ') for x in results]
	results  = [x for x in results if len(x) > 10]
	print_list(results)
	return results


def get_page_text(url):
	return nltk.clean_html(urllib.urlopen(url).read())

def pos(text):
	return nltk.pos_tag(nltk.word_tokenize(text))

def filter_results(results):
	# results = [x for x in results if not (x[0][1] == 'NN' and len(x) == 1)]
	results = [x for x in results if  not (fdist[x[0][0].lower().strip('.,?!')] > 0 and len(x) == 1)]
	results = [x for x in results if not (x[0][0].lower() in stop_words and len(x) == 1)]
	results = [x for x in results if not (len(x) > 2)]
	return results


def	freq(string):
	count = int(urllib.urlopen('http://85.250.92.138:81/'+string).read().strip())
	return count

def print_list(l):
	try:
		for item in l:
			print item
		print len(l)
	except Exception, e:
		app.logger.error(e)

def ne(text):
	part_of_speech = pos(text)
	return nltk.ne_chunk(part_of_speech)

# old had problem in the parsing of nltk so I use bs which spearate
# def main(url):
# 	text = get_page_text(url)
# 	part_of_speech = pos(text)
# 	results = []
# 	current_names = []
# 	for w in part_of_speech:
# 		if w[1] in ('NN','NNP','NNS'):
# 			current_names.append(w)
# 			if w[0].endswith('.'):
# 				results.append(current_names)
# 				current_names = []
# 		else:
# 			if current_names:
# 				results.append(current_names)
# 			current_names = []
# 	if results and current_names and results[-1] != current_names:
# 		results.append(current_names)
# 	results = filter_results(results)
# 	print_list(results)
# 	return results

def main2(url):
	texts = clean_with_bs(url)
	all_results = []
	for text in texts:
		# app.logger.error('text: '+ text)
		part_of_speech = pos(text)
		results = []
		current_names = []
		for w in part_of_speech:
			if w[1] == 'NNP':#('NN','NNP','NNS'):
				current_names.append(w)
				if w[0].endswith('.'):
					results.append(current_names)
					current_names = []
			else:
				if current_names:
					results.append(current_names)
				current_names = []
		if results and current_names and results[-1] != current_names:
			results.append(current_names)
		results = filter_results(results)
		all_results.extend(results)
	print_list(all_results)
	return all_results

@app.route('/')
def main_request():
	url = request.args.get('url')
	return json.dumps(main2(url))


