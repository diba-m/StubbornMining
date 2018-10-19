FROM python:2.7.14-wheezy

COPY requirements.txt .
RUN pip install -r requirements.txt
RUN pip install git+https://github.com/simonmulser/python-bitcoinlib@simcoin

RUN \
	apt-get update &&\
	apt-get install git

RUN git clone https://github.com/simonmulser/pycoin.git
RUN cd pycoin &&\
    git checkout simcoin &&\
    python setup.py install

COPY selfish_proxy /code

WORKDIR /code

CMD ["python", "main.py"]
