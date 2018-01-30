# Pipeline Messenger
Pipeline Messenger is an encrypted messaging system for Android devices that allows users to communicate in a secure manner. It enables users to communicate securely in one-to-one or many-to-many conversations with peace of mind.

<p align="center">
  <img src="https://i.imgur.com/TNAYfvO.png" width="300"> <img src="https://i.imgur.com/aLt8Utb.png" width="300">
</p>

Pipeline relies on an encryption protocol similar to the OpenPGP standard for email encryption. Public-private key RSA encryption is used to share AES keys between conversation participants, these keys are in turn used to encrypt message contents. This system ensures that messages can only be read on devices of conversation participants, and not malicious third parties attempting to intercept traffic.

Messages are also signed and verified using this public-private keypair, ensuring that messages are not altered during delivery.
