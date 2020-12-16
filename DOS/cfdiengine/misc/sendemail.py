from email import encoders
from email.mime.base import MIMEBase
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import smtplib
import os

class SendEmailError(Exception):
    def __init__(self, message = None):
        self.message = message
    def __str__(self):
        return self.message

class SendEmail(object):
    '''
    Function object that sends email with attachment.
    To have an account with smtp ssl is required
    '''

    __DEFAULT_FROMADDR = 'johndove@localhost'
    __DEFAULT_SMTP = 'localhost'
    __DEFAULT_PORT = 587
    __DEFAULT_PASSWD = ''

    def __init__(self, logger, **kwargs):
        self.logger = logger
        self.server_config = {
            'HOST': kwargs.get('host', self.__DEFAULT_SMTP),
            'PORT': kwargs.get('port', self.__DEFAULT_PORT),
            'PASSWD': kwargs.get('passwd', self.__DEFAULT_PASSWD)
        }

    def __call__(self, **kwargs):
        '''Sends email through smtp ssl with one or more attachments'''
        self.msg_config = {
            'FROMADDR': kwargs.get('fromaddr', self.__DEFAULT_FROMADDR),
            'TOADDR': kwargs.get('toaddr', None),
            'SUBJECT': kwargs.get('subject', None),
            'MSG': kwargs.get('msg', None),
            'ATTACHMENTS': kwargs.get('attachments', None)
        }

        try:
            outer = self.__conform_body(MIMEMultipart())
        except:
            raise SendEmailError("Unable to conform base text message")

        # Add the attachments to the message
        for item in self.msg_config['ATTACHMENTS']:
            try:
                with open(item, 'rb') as fp:
                    part = MIMEBase('application', "octet-stream")
                    part.set_payload(fp.read())
                encoders.encode_base64(part)
                part.add_header('Content-Disposition', 'attachment', filename=os.path.basename(item))
                outer.attach(part)
            except:
                raise SendEmailError("Unable to open one of the attachments")

        try:
            self.__dialog(outer)
        except:
            raise SendEmailError("It was nos possible to correctly dialog with server")

    def __conform_body(self, m):
        '''Conforms message body'''

        m['From'] = self.msg_config['FROMADDR']
        m['To'] = self.msg_config['TOADDR']
        m['Subject'] = self.msg_config['SUBJECT']
        body = self.msg_config['MSG']
        m.attach(MIMEText(body, 'plain'))
        return m

    def __dialog(self, m):
        '''Dialogs with smtp server'''
        server = smtplib.SMTP_SSL('{}:{}'.format(
            self.server_config['HOST'],
            self.server_config['PORT']))
        server.ehlo()
        server.login(
            self.msg_config['FROMADDR'],
            self.server_config['PASSWD']
        )
        text = m.as_string()
        server.sendmail(
            self.msg_config['FROMADDR'], 
            self.msg_config['TOADDR'], 
            text
        )
        server.quit()
